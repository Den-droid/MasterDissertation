import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { RefreshTokenDto, SignInDto, TokensDto } from "../models/auth.model";
import { Observable } from "rxjs";
import { JWTTokenService } from "./jwt-token.service";
import { baseUrl } from "../constants/url.constant";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private url: string = baseUrl + "/auth";

  constructor(private readonly httpClient: HttpClient, private readonly jwtService: JWTTokenService) {
  }

  signIn(signInDto: SignInDto): Observable<TokensDto> {
    return this.httpClient.post<TokensDto>(`${this.url}/signIn/apiKey`, signInDto);
  }

  refreshToken(refreshTokenDto: RefreshTokenDto): Observable<TokensDto> {
    return this.httpClient.put<TokensDto>(`${this.url}/refreshToken`, refreshTokenDto);
  }

  isAuthenticated(): boolean {
    return this.jwtService.getToken() != null;
  }

  logout() {
    this.jwtService.clearTokens();
  }
}
