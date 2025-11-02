import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { universityLabels } from '../../shared/translations/university.translation';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-university-modal',
  templateUrl: './university-modal.component.html',
  imports: [FormsModule, ReactiveFormsModule]
})
export class UniversityModalComponent {
  @Input() title = '';
  @Input() errorSubject$!: Subject<string>;

  private _inputValue = '';
  @Input()
  set inputValue(val: string) {
    this._inputValue = val;
    if (this.form) {
      this.form.get('name')?.setValue(val);
    }
  }
  get inputValue() {
    return this._inputValue;
  }

  @Output() saveAttempt = new EventEmitter<string>();

  requiredMessage = universityLabels['name-required'];
  errorMessage!: string;

  form!: FormGroup;

  constructor(public activeModal: NgbActiveModal, private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.errorSubject$.subscribe(msg => {
      this.errorMessage = msg;
    });

    this.form = this.fb.group({
      name: [this._inputValue, [Validators.required]]
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
