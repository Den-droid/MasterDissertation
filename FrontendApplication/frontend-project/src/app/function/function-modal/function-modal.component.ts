import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, Validators, ReactiveFormsModule, FormControl } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Subject } from 'rxjs';
import { ActionType } from '../../shared/constants/action-type';
import { SubjectDto } from '../../shared/models/subject.model';
import { UniversityDto } from '../../shared/models/university.model';
import { AddFunctionDto, FunctionDto, UpdateFunctionDto } from '../../shared/models/function.model';
import { functionLabels } from '../../shared/translations/function.translation';
import { decimalValidator, integerValidator } from '../../shared/validators/number.validator';
import { validationMessages } from '../../shared/translations/common.translation';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-function-modal',
  templateUrl: './function-modal.component.html',
  imports: [FormsModule, ReactiveFormsModule, CommonModule]
})
export class FunctionModalComponent {
  @Input() title = '';
  @Input() errorSubject$!: Subject<string>;
  @Input() actionType!: ActionType;

  @Input()
  set inputValue(val: FunctionDto) {
    if (this.form) {
      this.form.get('text')?.setValue(val.text);
      this.form.get('variablesCount')?.setValue(val.variablesCount);
      this.form.get('university')?.setValue(val.subject.university.id);
      this.form.get('subject')?.setValue(val.subject.id);

      if (val.minValues.length > 0) {
        (this.form.get('minValues') as FormGroup).get('0')?.setValue(val.minValues[0]);
        for (let i = 1; i < val.minValues.length; i++) {
          (this.form.get('minValues') as FormGroup).addControl(i.toString(),
            new FormControl(val.minValues[i], decimalValidator()));
        }
      }

      if (val.maxValues.length > 0) {
        (this.form.get('maxValues') as FormGroup).get('0')?.setValue(val.maxValues[0]);
        for (let i = 0; i < val.maxValues.length; i++) {
          (this.form.get('maxValues') as FormGroup).addControl(i.toString(),
            new FormControl(val.maxValues[i], decimalValidator()));
        }
      }
    }
  }

  private _universities: UniversityDto[] = [];
  @Input()
  set universities(val: UniversityDto[]) {
    this._universities = val;
  }
  get universities() {
    return this._universities;
  }

  private _subjects: SubjectDto[] = [];
  @Input()
  set subjects(val: SubjectDto[]) {
    this._subjects = val;
    if (this.form.value.university) {
      this.displayedSubjects = val.filter(s => s.university.id == this.form.value.university);
    } else {
      this.displayedSubjects = val;
    }
  }
  get subjects() {
    return this._subjects;
  }

  @Output() saveAttempt = new EventEmitter();

  displayedSubjects!: SubjectDto[];

  functionRequiredMessage = functionLabels['text-required'];
  subjectRequiredMessage = functionLabels['subject-selection-required'];
  variablesCountRequiredMessage = functionLabels['variables-count-required'];
  minMaxValueRequiredMessage = functionLabels['min-max-value-required'];
  integerRequiredMessage = validationMessages['integer-required'];
  decimalRequiredMessage = validationMessages['decimal-required'];
  errorMessage!: string;

  form!: FormGroup;

  constructor(public activeModal: NgbActiveModal, private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.errorSubject$.subscribe(msg => {
      this.errorMessage = msg;
    });

    this.form = this.fb.group({
      text: ['', [Validators.required]],
      variablesCount: ['', [Validators.required, integerValidator()]],
      minValues: this.fb.group({}),
      maxValues: this.fb.group({}),
      university: [''],
      subject: ['', [Validators.required]]
    });

    (this.form.get('minValues') as FormGroup).addControl('0', new FormControl('', decimalValidator()));
    (this.form.get('maxValues') as FormGroup).addControl('0', new FormControl('', decimalValidator()));

    this.form.get('university')?.valueChanges.subscribe({
      next: (value: string) => {
        if (Number.parseInt(value) == -1) {
          this.displayedSubjects = this.subjects;
        } else {
          this.displayedSubjects = this.subjects.filter(s => s.university.id == Number.parseInt(value));
          if (this.displayedSubjects.filter(ds => ds.id == this.form.value.subject).length === 0) {
            this.form.get('subject')?.setValue('');
          }
        }
      }
    })
  }

  get f() {
    return this.form.controls;
  }

  save() {
    this.validate();

    if (this.form.invalid) {
      return;
    }

    const minValuesToSend = Object.values(this.form.value.minValues)
      .filter(mv => mv !== '')
      .map(mv => Number.parseInt(mv as string));

    const maxValuesToSend = Object.values(this.form.value.maxValues)
      .filter(mv => mv !== '')
      .map(mv => Number.parseInt(mv as string));

    if (minValuesToSend.length === 0 && maxValuesToSend.length === 0) {
      this.errorMessage = this.minMaxValueRequiredMessage;
      return;
    }

    if (this.actionType === ActionType.CREATE) {
      let dto = new AddFunctionDto(this.form.value.text, Number.parseInt(this.form.value.variablesCount),
        minValuesToSend, maxValuesToSend, Number.parseInt(this.form.value.subject));

      this.saveAttempt.emit(dto);
    } else {
      let dto = new UpdateFunctionDto(this.form.value.text, Number.parseInt(this.form.value.variablesCount),
        minValuesToSend, maxValuesToSend, Number.parseInt(this.form.value.subject));

      this.saveAttempt.emit(dto);
    }
  }

  validate() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
  }

  removeMaxValue(index: number) {
    const group = (this.form.get('maxValues') as FormGroup);
    if (Object.keys(group.controls).length === 1) {
      group.get(Object.keys(group.controls)[index])?.setValue('');
    } else {
      group.removeControl(Object.keys(group.controls)[index]);
    }
  }

  addMaxValue() {
    const group = (this.form.get('maxValues') as FormGroup);
    const controlNames = Object.keys(group.controls);
    group.addControl(((Number.parseInt(controlNames[controlNames.length - 1]) + 1)).toString(), new FormControl('',
      decimalValidator()
    ));
  }

  removeMinValue(index: number) {
    const group = (this.form.get('minValues') as FormGroup);
    if (Object.keys(group.controls).length === 1) {
      group.get(Object.keys(group.controls)[index])?.setValue('');
    }
    else {
      group.removeControl(Object.keys(group.controls)[index]);
    }
  }

  addMinValue() {
    const group = (this.form.get('minValues') as FormGroup);
    const controlNames = Object.keys(group.controls);
    group.addControl(((Number.parseInt(controlNames[controlNames.length - 1]) + 1)).toString(), new FormControl('',
      decimalValidator()
    ));
  }

  isLastMaxValue(index: number) {
    const group = (this.form.get('maxValues') as FormGroup);
    const controlNames = Object.keys(group.controls);
    return controlNames.length - 1 === index;
  }

  isLastMinValue(index: number) {
    const group = (this.form.get('minValues') as FormGroup);
    const controlNames = Object.keys(group.controls);
    return controlNames.length - 1 === index;
  }

  getMinFormControlName(index: number) {
    const group = (this.form.get('minValues') as FormGroup);
    return Object.keys(group.controls)[index];
  }

  getMaxFormControlName(index: number) {
    const group = (this.form.get('maxValues') as FormGroup);
    return Object.keys(group.controls)[index];
  }

  getMinValues() {
    return Object.values(this.form.value.minValues);
  }

  getMaxValues() {
    return Object.values(this.form.value.maxValues);
  }
}

