import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { ApiKeyDto } from "../models/user.model";
import { JWTTokenService } from "./jwt-token.service";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private url: string = baseUrl + "/users";

  constructor(private readonly httpClient: HttpClient, private readonly jwtService: JWTTokenService) {
  }

  getApiKey(userId: number): Observable<ApiKeyDto> {
    return this.httpClient.put<ApiKeyDto>(`${this.url}/${userId}/apiKey`, null)
  }
}
