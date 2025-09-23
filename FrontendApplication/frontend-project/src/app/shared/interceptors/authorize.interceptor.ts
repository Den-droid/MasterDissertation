import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, concatMap, filter, finalize, take } from 'rxjs/operators';
import { Router } from '@angular/router';
import { RefreshTokenDto, TokensDto } from '../models/auth.model';
import { AuthService } from '../services/auth.service';
import { JWTTokenService } from '../services/jwt-token.service';

export const authorizeInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn):
  Observable<HttpEvent<any>> => {
  const jwtService = inject(JWTTokenService);
  const authService = inject(AuthService);
  const router = inject(Router);

  let isRefreshingToken = false;
  const tokenRefreshed$ = new BehaviorSubject<boolean>(false);

  const setHeader = (request: HttpRequest<any>) => {
    const token = jwtService.getToken();
    return token ? request.clone({ setHeaders: { Authorization: 'Bearer ' + token } }) : request;
  };

  const handle401Error = (request: HttpRequest<any>, nextFn: HttpHandlerFn): Observable<any> => {
    if (request.url.endsWith('/api/auth/signin')) {
      return nextFn(setHeader(request));
    }

    if (isRefreshingToken) {
      return tokenRefreshed$.pipe(
        filter(Boolean),
        take(1),
        concatMap(() => nextFn(setHeader(request)))
      );
    }

    isRefreshingToken = true;
    tokenRefreshed$.next(false);

    return authService.refreshToken(new RefreshTokenDto(jwtService.getRefreshToken())).pipe(
      concatMap((res: TokensDto) => {
        jwtService.setToken(res.accessToken);
        jwtService.setRefreshToken(res.refreshToken);
        tokenRefreshed$.next(true);
        return nextFn(setHeader(request));
      }),
      catchError(err => {
        authService.logout();
        router.navigateByUrl('/auth/signin');
        return throwError(() => err);
      }),
      finalize(() => {
        isRefreshingToken = false;
      })
    );
  };

  return next(setHeader(req)).pipe(
    catchError(err => {
      if (err.status === 401) {
        return handle401Error(req, next);
      }
      return throwError(() => err);
    })
  );
};
