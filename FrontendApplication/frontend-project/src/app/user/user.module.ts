import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ApikeyComponent } from './apikey/apikey.component';
import { authTitles } from '../shared/translations/auth.translation';

const studentRoutes: Routes = [
  { path: "apikey", component: ApikeyComponent, title: authTitles['api-key'] }
]

@NgModule({
  imports: [
    RouterModule.forChild(studentRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class UserModule { }
