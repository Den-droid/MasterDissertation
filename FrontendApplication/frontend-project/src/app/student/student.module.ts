import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AssignmentsComponent } from './assignment-list/assignment-list.component';
import { studentTitles } from '../shared/translations/student.translation';

const studentRoutes: Routes = [
  { path: "assignments", component: AssignmentsComponent, title: studentTitles.assignments },
  { path: "assignments/:id", redirectTo: "", title: studentTitles.assignment }
]

@NgModule({
  imports: [
    RouterModule.forChild(studentRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class StudentModule { }
