import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AssignmentRestrictionType, AssignmentRestrictionTypeLabel } from '../../constants/assignment-restriction-type';
import { DefaultRestrictionLevel, DefaultRestrictionLevelLabel } from '../../constants/default-restriction-level.constant';
import { ModalRestrictionDto, RestrictionTypeDto } from '../../models/restriction.model';
import { AssignmentRestrictionService } from '../../services/assignment-restriction.service';
import { validationMessages } from '../../translations/common.translation';
import { restrictionValidation } from '../../translations/restriction.translation';
import { datetimeValidator } from '../../validators/datetime.validator';
import { integerValidator } from '../../validators/number.validator';

@Component({
  selector: 'app-restriction-modal',
  templateUrl: './restriction-modal.component.html',
  imports: [FormsModule, ReactiveFormsModule]
})
export class RestrictionModalComponent {
  @Input() title = '';
  @Input() isInputRestrictionTypeDifferent!: boolean;
  @Input() inputRestritionTypeLevel!: DefaultRestrictionLevel;

  @Input()
  set inputValue(val: ModalRestrictionDto) {
    if (this.isInputRestrictionTypeDifferent) {
      this.defaultRestrictionAnotherLevel = val;
    } else {
      if (this.form) {
        this.form.removeControl('attemptsRemaining');
        if (val.restrictionType == AssignmentRestrictionType.N_ATTEMPTS) {
          this.form.get('restrictionType')?.setValue(AssignmentRestrictionType.N_ATTEMPTS);
          this.form.addControl('attemptsRemaining', new FormControl(0,
            [Validators.required, integerValidator()]));
          this.form.get('attemptsRemaining')?.setValue(val.attemptsRemaining);
        }
        else if (val.restrictionType == AssignmentRestrictionType.N_MINUTES) {
          this.form.get('restrictionType')?.setValue(AssignmentRestrictionType.N_MINUTES);
          this.form.addControl('minutesToDo', new FormControl(0,
            [Validators.required, integerValidator()]));
          this.form.get('minutesToDo')?.setValue(val.minutesToDo);
        }
        else {
          this.form.get('restrictionType')?.setValue(AssignmentRestrictionType.DEADLINE);
          this.form.addControl('deadline', new FormControl('',
            [Validators.required, datetimeValidator()]));
          this.form.get('deadline')?.setValue(val.deadline);
        }
      }
    }
  }

  @Output() saveAttempt = new EventEmitter();

  form!: FormGroup;
  restrictionTypes: RestrictionTypeDto[] = [];
  defaultRestrictionAnotherLevel!: ModalRestrictionDto;

  integerRequired = validationMessages['integer-required'];
  datetimeRequired = validationMessages['datetime-required'];

  attemptsRequired = restrictionValidation['attempts-required'];
  minutesRequired = restrictionValidation['minutes-required'];
  deadlineRequired = restrictionValidation['deadline-required'];

  constructor(public activeModal: NgbActiveModal, private fb: FormBuilder,
    private restrictionService: AssignmentRestrictionService) {
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      restrictionType: [AssignmentRestrictionType.N_ATTEMPTS, [Validators.required]],
      attemptsRemaining: ['', [Validators.required, integerValidator()]]
    });

    this.form.get('restrictionType')?.valueChanges.subscribe(value => {
      if (this.form.get('attemptsRemaining'))
        this.form.removeControl('attemptsRemaining');
      else if (this.form.get('minutesToDo'))
        this.form.removeControl('minutesToDo');
      else if (this.form.get('deadline'))
        this.form.removeControl('deadline');

      if (value == AssignmentRestrictionType.N_ATTEMPTS) {
        this.form.addControl('attemptsRemaining', new FormControl('',
          [Validators.required, integerValidator()]));
      }
      else if (value == AssignmentRestrictionType.N_MINUTES) {
        this.form.addControl('minutesToDo', new FormControl('',
          [Validators.required, integerValidator()]));
      }
      else {
        this.form.addControl('deadline', new FormControl('',
          [Validators.required, datetimeValidator()]));
      }
    }
    )

    this.getRestrictionTypes();
  }

  getRestrictionTypes() {
    this.restrictionService.getRestrictionTypes().subscribe({
      next: (dto: RestrictionTypeDto[]) => {
        this.restrictionTypes = dto;
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

    let restrictionTypeNumber = Number.parseInt(this.form.value.restrictionType);

    if (restrictionTypeNumber == AssignmentRestrictionType.N_ATTEMPTS) {
      this.saveAttempt.emit(new ModalRestrictionDto(restrictionTypeNumber, Number.parseInt(this.form.value.attemptsRemaining),
        null, null));
    }
    else if (restrictionTypeNumber == AssignmentRestrictionType.N_MINUTES) {
      this.saveAttempt.emit(new ModalRestrictionDto(restrictionTypeNumber, null, Number.parseInt(this.form.value.minutesToDo),
        null));
    }
    else if (restrictionTypeNumber == AssignmentRestrictionType.DEADLINE) {
      this.saveAttempt.emit(new ModalRestrictionDto(restrictionTypeNumber, null, null, this.form.value.deadline));
    }
  }

  validate() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
  }

  getRestrictionTypeLabel(type: number) {
    const key = Object.keys(AssignmentRestrictionType).find(
      k => AssignmentRestrictionType[k as keyof typeof AssignmentRestrictionType] === type
    );
    return AssignmentRestrictionTypeLabel[key as keyof typeof AssignmentRestrictionTypeLabel];
  }

  isAttemptsRemaining() {
    return this.form.value.restrictionType == AssignmentRestrictionType.N_ATTEMPTS
  }

  isMinutesToDo() {
    return this.form.value.restrictionType == AssignmentRestrictionType.N_MINUTES
  }

  isDeadline() {
    return this.form.value.restrictionType == AssignmentRestrictionType.DEADLINE
  }

  isAnotherLevelRestrictionAttemptsRemaining() {
    return this.defaultRestrictionAnotherLevel.restrictionType == AssignmentRestrictionType.N_ATTEMPTS
  }

  isAnotherLevelRestrictionMinutesToDo() {
    return this.defaultRestrictionAnotherLevel.restrictionType == AssignmentRestrictionType.N_MINUTES
  }

  isAnotherLevelRestrictionDeadline() {
    return this.defaultRestrictionAnotherLevel.restrictionType == AssignmentRestrictionType.DEADLINE
  }

  getAnotherLeverRestrictionLabel(value: number) {
    const key = Object.keys(DefaultRestrictionLevel).find(
      k => DefaultRestrictionLevel[k as keyof typeof DefaultRestrictionLevel] === value
    );
    return DefaultRestrictionLevelLabel[key as keyof typeof DefaultRestrictionLevelLabel];
  }
}
