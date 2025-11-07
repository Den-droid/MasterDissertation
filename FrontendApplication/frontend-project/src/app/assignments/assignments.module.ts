import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { assignmentTitles } from '../shared/translations/assignment.translation';
import { AssignmentComponent } from './assignment/assignment.component';
import { AssignmentListComponent } from './assignment-list/assignment-list.component';

const assignmentsRoutes: Routes = [
  { path: "", component: AssignmentListComponent, title: assignmentTitles.assignments },
  { path: ":id", component: AssignmentComponent, title: assignmentTitles.assignment }
]

@NgModule({
  imports: [
    RouterModule.forChild(assignmentsRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class AssignmentsModule { }
