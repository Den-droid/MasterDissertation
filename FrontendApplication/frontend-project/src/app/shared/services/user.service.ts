import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { ApiKeyDto, UserDto } from "../models/user.model";
import { JWTTokenService } from "./jwt-token.service";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private url: string = baseUrl + "/users";

  constructor(private readonly httpClient: HttpClient, private readonly jwtService: JWTTokenService) {
  }

  getByUserId(userId: number): Observable<UserDto> {
    return this.httpClient.get<UserDto>(`${this.url}/${userId}`)
  }

  get(): Observable<UserDto[]> {
    return this.httpClient.get<UserDto[]>(`${this.url}`)
  }

  getStudents(): Observable<UserDto[]> {
    const params = new HttpParams()
      .set('roleId', 1)

    return this.httpClient.get<UserDto[]>(`${this.url}`, { params })
  }

  getApiKey(userId: number): Observable<ApiKeyDto> {
    return this.httpClient.put<ApiKeyDto>(`${this.url}/${userId}/apiKey`, null)
  }

  approve(userId: number): Observable<any> {
    return this.httpClient.put<ApiKeyDto>(`${this.url}/${userId}/approve`, null)
  }

  reject(userId: number): Observable<any> {
    return this.httpClient.put<ApiKeyDto>(`${this.url}/${userId}/reject`, null)
  }
}
