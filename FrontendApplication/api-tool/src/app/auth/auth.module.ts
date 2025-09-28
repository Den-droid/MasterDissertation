import { NgModule } from '@angular/core';
import { SignInComponent } from './sign-in/sign-in.component';
import { RouterModule, Routes } from '@angular/router';
import { authTitles } from '../shared/translations/auth.translation';

const authRoutes: Routes = [
  { path: "signin", component: SignInComponent, title: authTitles.login },
  { path: "**", redirectTo: "/error/404" }
]

@NgModule({
  imports: [
    RouterModule.forChild(authRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class AuthModule { }
