import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authTitles } from '../shared/translations/auth.translation';
import { ApikeyComponent } from './apikey/apikey.component';

const userRoutes: Routes = [
  { path: "", component: ApikeyComponent, title: authTitles['api-key'] }
]

@NgModule({
  imports: [
    RouterModule.forChild(userRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class UserModule { }
