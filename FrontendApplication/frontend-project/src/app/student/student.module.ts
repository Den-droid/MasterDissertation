import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { studentTitles } from '../shared/translations/student.translation';
import { AssignmentsComponent } from './assignment-list/assignment-list.component';

const studentRoutes: Routes = [
  { path: "assignments", component: AssignmentsComponent, title: studentTitles.assignments }
]

@NgModule({
  imports: [
    RouterModule.forChild(studentRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class StudentModule { }
