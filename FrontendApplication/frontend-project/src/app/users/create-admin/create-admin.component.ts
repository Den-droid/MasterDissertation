import { CommonModule, Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CreateAdminDto } from '../../shared/models/user.model';
import { UserService } from '../../shared/services/user.service';
import { authLabels } from '../../shared/translations/auth.translation';
import { usersPageTitles } from '../../shared/translations/user.translation';

@Component({
  selector: 'app-user-create-admin',
  templateUrl: './create-admin.component.html',
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class CreateAdminComponent implements OnInit {
  form!: FormGroup;
  error = false;
  errorMessage = '';

  title = usersPageTitles['create-admin'];
  firstNameRequired = authLabels['first-name-required'];
  lastNameRequired = authLabels['last-name-required'];
  emailRequired = authLabels['email-address-required'];
  emailIncorrect = authLabels['email-address-incorrect'];
  passwordRequired = authLabels['password-required'];
  passwordTooShort = authLabels['password-less-than-8-chars'];
  passwordFormatIncorrect = authLabels['password-format-incoorect'];
  confirmPasswordRequired = authLabels['confirm-password-required'];
  passwordNotMatchConfirm = authLabels['password-not-match-confirm'];

  constructor(private readonly router: Router,
    private fb: FormBuilder, private location: Location, private userService: UserService
  ) {

  }

  ngOnInit(): void {
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
        confirmPassword: ['', [Validators.required]]
      },
      { validators: this.passwordMatchValidator }
    );
  }

  createAdmin() {
    this.validate();

    if (this.form.invalid) {
      return;
    }

    let createAdminDto = new CreateAdminDto(this.form.value.email, this.form.value.password,
      this.form.value.firstName, this.form.value.lastName);

    this.userService.createAdmin(createAdminDto).subscribe({
      error: (error: any) => {
        this.error = true;
        this.errorMessage = error?.error?.message;
      },
      complete: () => {
        this.router.navigate([`/`, `users`]);
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
