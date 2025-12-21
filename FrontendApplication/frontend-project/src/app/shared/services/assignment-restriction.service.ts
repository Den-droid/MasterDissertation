import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { baseUrl } from "../constants/url.constant";
import { Observable } from "rxjs";
import { DefaultRestrictionDto, ReadableDefaultRestrictionDto, ReadableRestrictionDto, RestrictionDto, RestrictionTypeDto } from "../models/restriction.model";

@Injectable({
    providedIn: 'root'
})
export class AssignmentRestrictionService {
    private url: string = baseUrl + '/assignmentRestrictions';

    constructor(private readonly httpClient: HttpClient) {
    }

    getRestrictionTypes(): Observable<RestrictionTypeDto[]> {
        return this.httpClient.get<RestrictionTypeDto[]>(`${this.url}/restrictionTypes`);
    }

    getCurrent(userAssignmentId: number): Observable<ReadableRestrictionDto> {
        let params = new HttpParams()
            .set('userAssignmentId', userAssignmentId)
        return this.httpClient.get<ReadableRestrictionDto>(`${this.url}`, { params });
    }

    getDefaultForUniversity(universityId: number): Observable<ReadableDefaultRestrictionDto[]> {
        let params = new HttpParams()
            .set('universityId', universityId)
        return this.httpClient.get<ReadableDefaultRestrictionDto[]>(`${this.url}/defaultRestrictions`, { params });
    }

    getDefaultForSubject(subjectId: number): Observable<ReadableDefaultRestrictionDto[]> {
        let params = new HttpParams()
            .set('subjectId', subjectId)
        return this.httpClient.get<ReadableDefaultRestrictionDto[]>(`${this.url}/defaultRestrictions`, { params });
    }

    getDefaultForFunction(functionId: number): Observable<ReadableDefaultRestrictionDto[]> {
        let params = new HttpParams()
            .set('functionId', functionId)
        return this.httpClient.get<ReadableDefaultRestrictionDto[]>(`${this.url}/defaultRestrictions`, { params });
    }

    setDefaultRestriction(defaultRestrictionDto: DefaultRestrictionDto): Observable<any> {
        return this.httpClient.put<any>(`${this.url}/setDefaultRestriction`, defaultRestrictionDto);
    }

    setRestriction(restrictionDto: RestrictionDto): Observable<any> {
        return this.httpClient.put<any>(`${this.url}/setRestriction`, restrictionDto);
    }
}