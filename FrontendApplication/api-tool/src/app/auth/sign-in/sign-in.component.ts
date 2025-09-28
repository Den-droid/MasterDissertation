import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { JWTTokenService } from '../../shared/services/jwt-token.service';
import { AuthService } from '../../shared/services/auth.service';
import { SignInDto, TokensDto } from '../../shared/models/auth.model';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { authLabels } from '../../shared/translations/auth.translation';

@Component({
  selector: 'app-auth-signIn',
  templateUrl: './sign-in.component.html',
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class SignInComponent {
  error = false;
  errorMessage = '';

  form!: FormGroup;

  keyRequiredMessage = authLabels['key-required'];

  constructor(private readonly router: Router, private readonly authService: AuthService,
    private jwtService: JWTTokenService, private fb: FormBuilder) {
    this.form = this.fb.group({
      apiKey: ['', [Validators.required]]
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

    let signInDto = new SignInDto(this.form.value.apiKey);

    this.authService.signIn(signInDto).subscribe({
      next: (result: TokensDto) => {
        this.jwtService.setToken(result.accessToken);
        this.jwtService.setRefreshToken(result.refreshToken);
      },
      error: (error: any) => {
        this.error = true;
        this.errorMessage = error.error.message;
      },
      complete: () => {
        this.router.navigate([`/apitool/tool`]);
      }
    });
  }

  validate() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
  }
}
