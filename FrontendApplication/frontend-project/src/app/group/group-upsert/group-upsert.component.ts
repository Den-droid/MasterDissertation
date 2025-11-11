import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";

@Component({
    selector: 'app-group-upsert',
    templateUrl: './group-upsert.component.html',
    imports: [FormsModule, ReactiveFormsModule, CommonModule]
})
export class GroupUpsertComponent implements OnInit {
    ngOnInit(): void {
    }
}