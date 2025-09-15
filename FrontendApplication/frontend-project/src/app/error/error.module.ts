import { NgModule } from '@angular/core';

import { RouterModule, Routes } from '@angular/router';
import { NotFoundComponent } from './not-found/not-found.component';
import { ForbiddenComponent } from './forbidden/forbidden.component';

const errorRoutes: Routes = [
  { path: "404", component: NotFoundComponent, title: 'Сторінку не знайдено' },
  { path: "403", component: ForbiddenComponent, title: 'Немає доступу' }
];

@NgModule({
  imports: [
    RouterModule.forChild(errorRoutes)
  ],
  providers: [RouterModule]
})
export class ErrorModule { }
