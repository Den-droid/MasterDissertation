import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ChangePasswordDto, ForgotPasswordDto, RefreshTokenDto, SignInDto, SignUpDto, TokensDto } from "../models/auth.model";
import { Observable } from "rxjs";
import { JWTTokenService } from "./jwt-token.service";
import { RoleName } from "../constants/roles.constant";
import { baseUrl } from "../constants/url.constant";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private url: string = baseUrl + "/auth";

  constructor(private readonly httpClient: HttpClient, private readonly jwtService: JWTTokenService) {
  }

  signIn(signInDto: SignInDto): Observable<TokensDto> {
    return this.httpClient.post<TokensDto>(`${this.url}/signIn/password`, signInDto);
  }

  signUp(signUpDto: SignUpDto): Observable<any> {
    return this.httpClient.post(`${this.url}/signUp`, signUpDto);
  }

  forgotPassword(forgotPasswordDto: ForgotPasswordDto): Observable<any> {
    return this.httpClient.post(`${this.url}/forgotPassword/create`, forgotPasswordDto);
  }

  existsByForgotPasswordToken(token: string): Observable<boolean> {
    const options = token ?
      {
        params: new HttpParams().set('token', token)
      } : {};

    return this.httpClient.get<boolean>(`${this.url}/forgotPassword/tokenExists`, options);
  }

  changePassword(token: string, changePasswordDto: ChangePasswordDto): Observable<any> {
    return this.httpClient.post(`${this.url}/forgotPassword/change/${token}`, changePasswordDto);
  }

  refreshToken(refreshTokenDto: RefreshTokenDto): Observable<TokensDto> {
    return this.httpClient.put<TokensDto>(`${this.url}/refreshToken`, refreshTokenDto);
  }

  isAuthenticated(): boolean {
    return this.jwtService.getToken() ? true : false;
  }

  isStudent(): boolean {
    return this.jwtService.getRoles()?.includes(RoleName.STUDENT) ?? false;
  }

  isTeacher(): boolean {
    return this.jwtService.getRoles()?.includes(RoleName.TEACHER) ?? false;
  }

  isAdmin(): boolean {
    return this.jwtService.getRoles()?.includes(RoleName.ADMIN) ?? false;
  }

  logout() {
    this.jwtService.clearTokens();
  }
}
