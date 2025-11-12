import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { IdDto } from "../models/common.model";
import { GroupDto, AddGroupDto, UpdateGroupDto } from "../models/group.model";

@Injectable({
    providedIn: 'root'
})
export class GroupService {
    private url: string = baseUrl + "/groups";

    constructor(private readonly httpClient: HttpClient) {
    }

    getAll(): Observable<GroupDto[]> {
        return this.httpClient.get<GroupDto[]>(`${this.url}`);
    }

    getById(id: number): Observable<GroupDto> {
        return this.httpClient.get<GroupDto>(`${this.url}/${id}`);
    }

    add(dto: AddGroupDto): Observable<IdDto> {
        return this.httpClient.post<IdDto>(`${this.url}`, dto);
    }

    update(id: number, dto: UpdateGroupDto): Observable<any> {
        return this.httpClient.put<any>(`${this.url}/${id}`, dto);
    }

    setStudents(id: number, userIds: number[]): Observable<any> {
        return this.httpClient.put<any>(`${this.url}/${id}/setStudents`, userIds);
    }

    setSubjects(id: number, subjectIds: number[]): Observable<any> {
        return this.httpClient.put<any>(`${this.url}/${id}/setSubjects`, subjectIds);
    }

    delete(id: number): Observable<any> {
        const queryParams = new HttpParams();
        queryParams.set('id', id);

        return this.httpClient.delete<any>(`${this.url}/${id}`, { params: queryParams });
    }
}