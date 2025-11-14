import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { PermissionDto, UpdatePermissionsDto } from "../models/permission.model";

@Injectable({
    providedIn: 'root'
})
export class PermissionService {
    private url: string = baseUrl + "/permissions";

    constructor(private readonly httpClient: HttpClient) {
    }

    getByUserId(userId: number): Observable<PermissionDto[]> {
        const params = new HttpParams()
            .set('userId', userId);
        return this.httpClient.get<PermissionDto[]>(`${this.url}`, { params })
    }

    updatePermissions(dto: UpdatePermissionsDto): Observable<any> {
        return this.httpClient.put<any>(`${this.url}`, dto)
    }
}