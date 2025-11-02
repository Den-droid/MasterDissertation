import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { IdDto } from "../models/common.model";
import { AddSubjectDto, UpdateSubjectDto, SubjectDto } from "../models/subject.model";

@Injectable({
    providedIn: 'root'
})
export class SubjectsService {
    private url: string = baseUrl + "/subjects";

    constructor(private readonly httpClient: HttpClient) {
    }

    getAll(): Observable<SubjectDto[]> {
        return this.httpClient.get<SubjectDto[]>(`${this.url}`);
    }

    getById(id: number): Observable<SubjectDto> {
        return this.httpClient.get<SubjectDto>(`${this.url}/${id}`);
    }

    add(dto: AddSubjectDto): Observable<IdDto> {
        return this.httpClient.post<IdDto>(`${this.url}`, dto);
    }

    update(id: number, dto: UpdateSubjectDto): Observable<any> {
        return this.httpClient.put<any>(`${this.url}/${id}`, dto);
    }

    delete(id: number): Observable<any> {
        const queryParams = new HttpParams();
        queryParams.set('id', id);

        return this.httpClient.delete<any>(`${this.url}/${id}`, { params: queryParams });
    }
}