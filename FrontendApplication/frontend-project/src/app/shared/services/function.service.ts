import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { AssignmentFunctionDto } from "../models/assignment.model";
import { IdDto } from "../models/common.model";
import { AddFunctionDto, FunctionDto, UpdateFunctionDto } from "../models/function.model";

@Injectable({
    providedIn: 'root'
})
export class FunctionService {
    private url: string = baseUrl + "/functions";

    constructor(private readonly httpClient: HttpClient) {
    }

    getAll(): Observable<FunctionDto[]> {
        return this.httpClient.get<FunctionDto[]>(`${this.url}`);
    }

    getById(id: number): Observable<FunctionDto> {
        return this.httpClient.get<FunctionDto>(`${this.url}/${id}`);
    }

    getByAssignmentIds(userAssignmentIds: number[]) {
        let params = new HttpParams();

        if (userAssignmentIds.length > 0) {
            userAssignmentIds.forEach(id => {
                params = params.append('userAssignmentIds', id.toString());
            });
        } else {
            params = params.append('userAssignmentIds', '');
        }
        return this.httpClient.get<AssignmentFunctionDto[]>(`${this.url}/getByAssignmentIds`, { params });
    }

    add(dto: AddFunctionDto): Observable<IdDto> {
        return this.httpClient.post<IdDto>(`${this.url}`, dto);
    }

    update(id: number, dto: UpdateFunctionDto): Observable<any> {
        return this.httpClient.put<any>(`${this.url}/${id}`, dto);
    }

    delete(id: number): Observable<any> {
        const queryParams = new HttpParams();
        queryParams.set('id', id);

        return this.httpClient.delete<any>(`${this.url}/${id}`, { params: queryParams });
    }
}