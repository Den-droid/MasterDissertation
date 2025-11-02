import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UniversityListComponent } from './university-list/university-list.component';
import { universityPageTitles } from '../shared/translations/university.translation';

const universityRoutes: Routes = [
  { path: "", component: UniversityListComponent, title: universityPageTitles.universities }
]

@NgModule({
  imports: [
    RouterModule.forChild(universityRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class UniversityModule { }
