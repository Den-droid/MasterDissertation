import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { usersPageTitles } from '../shared/translations/user.translation';
import { ApikeyComponent } from './apikey/apikey.component';

const userRoutes: Routes = [
  { path: "", component: ApikeyComponent, title: usersPageTitles['api-key'] }
]

@NgModule({
  imports: [
    RouterModule.forChild(userRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class UserModule { }
