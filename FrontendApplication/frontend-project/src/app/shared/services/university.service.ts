import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { baseUrl } from "../constants/url.constant";
import { Observable } from "rxjs";
import { UniversityDto } from "../models/university.model";

@Injectable({
    providedIn: 'root'
})
export class UniversityService {
    private url: string = baseUrl + "/universities";

    constructor(private readonly httpClient: HttpClient) {
    }

    getAll(): Observable<UniversityDto[]> {
        return this.httpClient.get<UniversityDto[]>(`${this.url}`);
    }
}
