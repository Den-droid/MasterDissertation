import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Subject } from 'rxjs';
import { ActionType } from '../../shared/constants/action-type';
import { AddSubjectDto, SubjectDto, UpdateSubjectDto } from '../../shared/models/subject.model';
import { UniversityDto } from '../../shared/models/university.model';
import { FunctionDto } from '../../shared/models/function.model';
import { functionLabels } from '../../shared/translations/function.translation';
import { integerValidator } from '../../shared/validators/number.validator';
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
      minValue: [''],
      maxValue: [''],
      university: [''],
      subject: ['', [Validators.required]]
    });

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



    // if (this.actionType === ActionType.CREATE) {
    //   this.saveAttempt.emit();
    // } else {
    //   this.saveAttempt.emit();
    // }
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
}
