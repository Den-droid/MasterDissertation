import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Subject } from 'rxjs';
import { subjectLabels } from '../../shared/translations/subject.translation';
import { ActionType } from '../../shared/constants/action-type';
import { AddSubjectDto, SubjectDto, UpdateSubjectDto } from '../../shared/models/subject.model';
import { UniversityDto } from '../../shared/models/university.model';
import { universityLabels } from '../../shared/translations/university.translation';

@Component({
  selector: 'app-subject-modal',
  templateUrl: './subject-modal.component.html',
  imports: [FormsModule, ReactiveFormsModule]
})
export class SubjectModalComponent {
  @Input() title = '';
  @Input() errorSubject$!: Subject<string>;
  @Input() actionType!: ActionType;

  @Input()
  set inputValue(val: SubjectDto) {
    if (this.form) {
      this.form.get('name')?.setValue(val.name);
      this.form.get('university')?.setValue(val.university.id);
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

  @Output() saveAttempt = new EventEmitter();

  subjectRequiredMessage = subjectLabels['name-required'];
  universityRequiredMessage = universityLabels['selection-required'];
  errorMessage!: string;

  form!: FormGroup;

  constructor(public activeModal: NgbActiveModal, private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.errorSubject$.subscribe(msg => {
      this.errorMessage = msg;
    });

    this.form = this.fb.group({
      name: ['', [Validators.required]],
      university: ['', [Validators.required]]
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

    if (this.actionType === ActionType.CREATE) {
      this.saveAttempt.emit(new AddSubjectDto(this.form.value.name, this.form.value.university));
    } else {
      this.saveAttempt.emit(new UpdateSubjectDto(this.form.value.name, this.form.value.university));
    }
  }

  validate() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
  }
}
