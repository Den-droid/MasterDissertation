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

@Component({
  selector: 'app-function-modal',
  templateUrl: './function-modal.component.html',
  imports: [FormsModule, ReactiveFormsModule]
})
export class FunctionModalComponent {
  @Input() title = '';
  @Input() errorSubject$!: Subject<string>;
  @Input() actionType!: ActionType;

  private _inputValue!: FunctionDto;
  @Input()
  set inputValue(val: FunctionDto) {
    this._inputValue = val;
    this.selectedSubject = val.subject;
    this.selectedUniversity = val.subject.university;

    if (this.form) {
      this.form.get('text')?.setValue(val.text);
      this.form.get('variablesCount')?.setValue(val.variablesCount);
      this.form.get('subject')?.setValue(val.subject.id);
      this.form.get('university')?.setValue(val.subject.university.id);

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
  get inputValue() {
    return this._inputValue;
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
    this.displayedSubjects = val;
  }
  get subjects() {
    return this._subjects;
  }

  @Output() saveAttempt = new EventEmitter();

  displayedSubjects!: SubjectDto[];

  selectedUniversity!: UniversityDto;
  selectedSubject!: SubjectDto;

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
      next: (value: any) => {
        if (value && Number.parseInt(value)) {
          this.displayedSubjects = this.subjects.filter(s => s.university.id == value);
          if (this.selectedSubject &&
            this.displayedSubjects.filter(ds => ds.id == this.selectedSubject.id).length === 0) {
            this.form.get('subject')?.setValue('');
          }
        } else {
          this.displayedSubjects = this.subjects;
        }
      }
    })

    this.form.get('subject')?.valueChanges.subscribe({
      next: (value: number) => {
        this.selectedSubject = this.subjects.filter(s => s.id == value)[0];
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

  isUniversitySelected(id: number) {
    return this.selectedUniversity && this.selectedUniversity.id === id;
  }

  isSubjectSelected(id: number) {
    return this.selectedSubject && this.selectedSubject.id === id;
  }

  removeMaxValue(index: number) {
    const group = (this.form.get('maxValues') as FormGroup);
    group.removeControl(Object.keys(group.controls)[index]);
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
    group.removeControl(Object.keys(group.controls)[index]);
  }

  addMinValue() {
    const group = (this.form.get('minValues') as FormGroup);
    const controlNames = Object.keys(group.controls);
    group.addControl(((Number.parseInt(controlNames[controlNames.length - 1]) + 1)).toString(), new FormControl('',
      decimalValidator()
    ));
  }

  isSingleMaxValue(index: number) {
    const group = (this.form.get('maxValues') as FormGroup);
    const controlNames = Object.keys(group.controls);
    return controlNames.length === 1 && index === 0;
  }

  isLastMaxValue(index: number) {
    const group = (this.form.get('maxValues') as FormGroup);
    const controlNames = Object.keys(group.controls);
    return controlNames.length - 1 === index;
  }

  isSingleMinValue(index: number) {
    const group = (this.form.get('minValues') as FormGroup);
    const controlNames = Object.keys(group.controls);
    return controlNames.length === 1 && index === 0;
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

