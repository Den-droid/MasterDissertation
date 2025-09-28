import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { JWTTokenService } from "./jwt-token.service";
import { ApiKeyDto } from "../models/user.model";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private url: string = baseUrl + "/users";

  constructor(private readonly httpClient: HttpClient, private readonly jwtService: JWTTokenService) {
  }

  getApiKey(userId: number): Observable<ApiKeyDto> {
    const options = userId ?
      {
        params: new HttpParams().set('userId', userId)
      } : {};
    return this.httpClient.put<ApiKeyDto>(`${this.url}/${userId}/apiKey`, null)
  }
}
