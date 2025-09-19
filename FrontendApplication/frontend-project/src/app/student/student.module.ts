import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { studentTitles } from '../shared/translations/student.translation';
import { StudentAssignmentsComponent } from './assignments/student-assignments.component';

const studentRoutes: Routes = [
  { path: "assignments", component: StudentAssignmentsComponent, title: studentTitles.assignments }
]

@NgModule({
  imports: [
    RouterModule.forChild(studentRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class StudentModule { }
