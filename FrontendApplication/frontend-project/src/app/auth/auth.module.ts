import { NgModule } from '@angular/core';
import { SignInComponent } from './sign-in/sign-in.component';
import { RouterModule, Routes } from '@angular/router';
import { SignUpComponent } from './sign-up/sign-up.component';
import { SignUpSuccessComponent } from './sign-up-success/sign-up-success.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ForgotPasswordSuccessComponent } from './forgot-password-success/forgot-password-success.component';
import { SetNewPasswordComponent } from './set-new-password/set-new-password.component';
import { authTitles } from '../shared/translations/auth.translation';

const authRoutes: Routes = [
  { path: "signin", component: SignInComponent, title: authTitles.login },
  { path: "signup", component: SignUpComponent, title: authTitles['sign-up'] },
  { path: "signup/success/:dummy", component: SignUpSuccessComponent, title: authTitles['sign-up'] },
  { path: "forgotpassword", component: ForgotPasswordComponent, title: authTitles['forgot-password'] },
  { path: "forgotpassword/success/:dummy", component: ForgotPasswordSuccessComponent, title: authTitles['forgot-password'] },
  { path: "forgotpassword/:token", component: SetNewPasswordComponent, title: authTitles['restore-password'] },
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
