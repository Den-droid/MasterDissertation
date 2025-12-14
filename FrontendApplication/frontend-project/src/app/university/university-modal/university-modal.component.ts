import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Subject } from 'rxjs';
import { validationMessages } from '../../shared/translations/common.translation';

@Component({
  selector: 'app-university-modal',
  templateUrl: './university-modal.component.html',
  imports: [FormsModule, ReactiveFormsModule]
})
export class UniversityModalComponent {
  @Input() title = '';
  @Input() errorSubject$!: Subject<string>;

  @Input()
  set inputValue(val: string) {
    if (this.form) {
      this.form.get('name')?.setValue(val);
    }
  }

  @Output() saveAttempt = new EventEmitter<string>();

  requiredMessage = validationMessages['name-required'];
  errorMessage!: string;

  form!: FormGroup;

  constructor(public activeModal: NgbActiveModal, private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.errorSubject$.subscribe(msg => {
      this.errorMessage = msg;
    });

    this.form = this.fb.group({
      name: ['', [Validators.required]]
    });
  }

  get f() {
    return this.form.controls;
  }

  save() {
    this.validate();

    if (this.form.invalid) {
      return;
    }

    this.saveAttempt.emit(this.form.value.name);
  }

  validate() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
  }
}
