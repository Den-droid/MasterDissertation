import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { v4 as uuidv4 } from 'uuid';
import { SignUpDto } from '../../shared/models/auth.model';
import { AuthService } from '../../shared/services/auth.service';
import { CommonModule, Location } from '@angular/common';
import { AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RoleLabel, RoleName } from '../../shared/constants/roles.constant';
import { authLabels } from '../../shared/translations/auth.translation';

@Component({
  selector: 'app-auth-signUp',
  templateUrl: './sign-up.component.html',
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class SignUpComponent implements OnInit {
  uuid = '';
  form!: FormGroup;
  error = false;
  errorMessage = '';

  firstNameRequired = authLabels['first-name-required'];
  lastNameRequired = authLabels['last-name-required'];
  emailRequired = authLabels['email-address-required'];
  emailIncorrect = authLabels['email-address-incorrect'];
  passwordRequired = authLabels['password-required'];
  passwordTooShort = authLabels['password-less-than-8-chars'];
  passwordFormatIncorrect = authLabels['password-format-incoorect'];
  confirmPasswordRequired = authLabels['confirm-password-required'];
  passwordNotMatchConfirm = authLabels['password-not-match-confirm'];

  constructor(private readonly router: Router, private readonly authService: AuthService,
    private fb: FormBuilder, private location: Location
  ) {

  }

  ngOnInit(): void {
    this.uuid = uuidv4();

    this.form = this.fb.group(
      {
        firstName: ['', [Validators.required]],
        lastName: ['', [Validators.required]],
        email: ['', [Validators.required, Validators.email]],
        password: [
          '',
          [
            Validators.required,
            Validators.minLength(8),
            Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).+$/),
          ],
        ],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  signUp() {
    this.validate();

    if (this.form.invalid) {
      return;
    }

    let signUpDto = new SignUpDto(this.form.value.email, this.form.value.password,
      this.form.value.firstName, this.form.value.lastName, RoleName.STUDENT);

    this.authService.signUp(signUpDto).subscribe({
      error: (error: any) => {
        this.error = true;
        this.errorMessage = error?.error?.message;
      },
      complete: () => {
        this.router.navigateByUrl("/auth/signup/success/" + this.uuid);
      }
    });
  }

  get f(): { [key: string]: AbstractControl } {
    return this.form.controls;
  }

  passwordMatchValidator(group: FormGroup) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
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
