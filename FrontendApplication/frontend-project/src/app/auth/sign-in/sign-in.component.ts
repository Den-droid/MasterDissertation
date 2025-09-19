import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ValidateEmails } from '../../shared/validators/emails.validator';
import { JWTTokenService } from '../../shared/services/jwt-token.service';
import { AuthService } from '../../shared/services/auth.service';
import { SignInDto, TokensDto } from '../../shared/models/auth.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { authLabels } from '../../shared/translations/auth.translation';

@Component({
  selector: 'app-auth-signIn',
  templateUrl: './sign-in.component.html',
  imports: [CommonModule, FormsModule, RouterLink]
})
export class SignInComponent {
  email = '';
  password = '';
  error = '';

  constructor(private readonly router: Router, private readonly authService: AuthService,
    private jwtService: JWTTokenService) {
  }

  signIn() {
    let validationResult = this.validate();
    if (validationResult.length > 0) {
      this.error = validationResult;
      return;
    }

    let signInDto = new SignInDto(this.email, this.password);

    this.authService.signIn(signInDto).subscribe({
      next: (result: TokensDto) => {
        this.jwtService.setToken(result.accessToken);
        this.jwtService.setRefreshToken(result.refreshToken);
      },
      error: (error: any) => {
        this.error = authLabels['user-not-exists-by-email-password'];
      },
      complete: () => {
        this.router.navigateByUrl("/user/profiles");
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
    if (this.password.length < 8) {
      return authLabels['password-incorrect'];
    }
    return '';
  }
}
