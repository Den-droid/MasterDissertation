import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { AuthService } from '../../shared/services/auth.service';
import { ChangePasswordDto } from '../../shared/models/auth.model';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { authLabels } from '../../shared/translations/auth.translation';

@Component({
  selector: 'app-auth-set-new-password',
  templateUrl: './set-new-password.component.html',
  styleUrls: ['./set-new-password.component.css'],
  imports: [FormsModule, CommonModule]
})
export class SetNewPasswordComponent implements OnInit {
  password = '';
  confirmPassword = '';
  error = '';
  token = ''

  constructor(private readonly router: Router, private readonly authService: AuthService,
    private readonly activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((data: Params) => {
      this.token = data['token'];
      this.authService.existsByForgotPasswordToken(this.token).subscribe({
        next: (result: boolean) => {
          if (!result) {
            this.router.navigateByUrl("/error/404");
          }
        }
      });
    });
  }

  setNewPassword() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let changePasswordDto = new ChangePasswordDto(this.password);

    this.authService.changePassword(this.token, changePasswordDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
      },
      complete: () => {
        this.router.navigateByUrl("/auth/signin");
      }
    });
  }

  validate(): string {
    if (this.password.length < 8) {
      return authLabels['password-incorrect'];
    } if (this.confirmPassword.length === 0) {
      return authLabels['confirm-password-required'];
    }
    if (this.confirmPassword !== this.password) {
      return authLabels['password-not-match-confirm'];
    }
    return '';
  }
}
