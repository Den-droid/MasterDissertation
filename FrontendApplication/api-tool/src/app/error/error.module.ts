import { NgModule } from '@angular/core';

import { RouterModule, Routes } from '@angular/router';
import { NotFoundComponent } from './not-found/not-found.component';
import { errorPagesTitles } from '../shared/translations/error.translation';

const errorRoutes: Routes = [
  { path: "404", component: NotFoundComponent, title: errorPagesTitles[404] }
];

@NgModule({
  imports: [
    RouterModule.forChild(errorRoutes)
  ],
  providers: [RouterModule]
})
export class ErrorModule { }
