import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { JWTTokenService } from '../../shared/services/jwt-token.service';
import { AuthService } from '../../shared/services/auth.service';
import { SignInDto, TokensDto } from '../../shared/models/auth.model';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { authLabels } from '../../shared/translations/auth.translation';

@Component({
  selector: 'app-auth-signIn',
  templateUrl: './sign-in.component.html',
  imports: [CommonModule, FormsModule, RouterLink, ReactiveFormsModule]
})
export class SignInComponent {
  error = false;
  errorMessage = '';

  form!: FormGroup;

  emailRequired = authLabels['email-address-required'];
  emailIncorrect = authLabels['email-address-incorrect'];
  passwordRequired = authLabels['password-required'];
  passwordTooShort = authLabels['password-less-than-8-chars'];


  constructor(private readonly router: Router, private readonly authService: AuthService,
    private jwtService: JWTTokenService, private location: Location, private fb: FormBuilder) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  get f() {
    return this.form.controls;
  }

  signIn() {
    this.validate();

    if (this.form.invalid) {
      return;
    }

    let signInDto = new SignInDto(this.form.value.email, this.form.value.password);

    console.log(signInDto)

    this.authService.signIn(signInDto).subscribe({
      next: (result: TokensDto) => {
        this.jwtService.setToken(result.accessToken);
        this.jwtService.setRefreshToken(result.refreshToken);
      },
      error: (error: any) => {
        this.error = true;
        this.errorMessage = authLabels['user-not-exists-by-email-password'];
      },
      complete: () => {
        if (this.authService.isStudent())
          this.router.navigate([`/student/assignments`]);
      }
    });
  }

  validate() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
  }

  goBack() {
    this.location.back();
  }
}
