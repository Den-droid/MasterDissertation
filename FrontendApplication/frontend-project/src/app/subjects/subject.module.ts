import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { subjectPageTitles } from '../shared/translations/subject.translation';
import { SubjectListComponent } from './subject-list/subject-list.component';

const subjectRoutes: Routes = [
  { path: "", component: SubjectListComponent, title: subjectPageTitles.subjects }
]

@NgModule({
  imports: [
    RouterModule.forChild(subjectRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class SubjectModule { }
