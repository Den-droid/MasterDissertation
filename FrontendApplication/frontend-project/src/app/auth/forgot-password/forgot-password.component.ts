import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ValidateEmails } from '../../shared/validators/emails.validator';
import { v4 as uuidv4 } from 'uuid';
import { AuthService } from '../../shared/services/auth.service';
import { ForgotPasswordDto } from '../../shared/models/auth.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { authLabels } from '../../shared/translations/auth.translation';

@Component({
  selector: 'app-auth-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
  imports: [CommonModule, FormsModule]
})
export class ForgotPasswordComponent implements OnInit {
  email = '';
  error = '';
  uuid = '';

  constructor(private readonly router: Router, private readonly authService: AuthService) {
  }

  ngOnInit(): void {
    this.uuid = uuidv4();
  }

  forgotPassword() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let forgotPasswordDto = new ForgotPasswordDto(this.email);

    this.authService.forgotPassword(forgotPasswordDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
      },
      complete: () => {
        this.router.navigateByUrl("/auth/forgotpassword/success/" + this.uuid);
      }
    });
  }

  validate(): string {
    if (this.email.length === 0) {
      return authLabels['email-address-required'];
    }
    if (!ValidateEmails(this.email)) {
      return authLabels['email-address-incorrect'];
    }
    return '';
  }
}
