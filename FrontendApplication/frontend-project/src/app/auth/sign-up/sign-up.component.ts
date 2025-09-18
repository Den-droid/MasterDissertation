import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ValidateEmails } from '../../shared/validators/emails.validator';
import { v4 as uuidv4 } from 'uuid';
import { SignUpDto } from '../../shared/models/auth.model';
import { AuthService } from '../../shared/services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { authLabels } from '../../shared/translations/auth.translation';

@Component({
  selector: 'app-auth-signUp',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css'],
  imports: [CommonModule, FormsModule]
})
export class SignUpComponent implements OnInit {
  email = '';
  password = '';
  confirmPassword = '';

  selectedScientist = 0;

  error = '';
  uuid = '';

  public set searchQuery(searchQuery: string) {

  }

  constructor(private readonly router: Router, private readonly authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.uuid = uuidv4();
  }

  signUp() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    } else {
      this.error = '';
    }

    let signUpDto = new SignUpDto(this.email, this.password);

    this.authService.signUp(signUpDto).subscribe({
      error: (error: any) => {
        this.error = error?.error?.error;
      },
      complete: () => {
        this.router.navigateByUrl("/auth/signup/success/" + this.uuid);
      }
    });
  }

  validate(): string {
    if (this.email.length === 0) {
      return authLabels['email-address-required'];
    } if (!ValidateEmails(this.email)) {
      return authLabels['email-address-incorrect'];
    }
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
