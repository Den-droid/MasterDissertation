import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AssignmentService } from '../../shared/services/assignment.service';
import { JWTTokenService } from '../../shared/services/jwt-token.service';
import { parseToNumber } from '../../shared/helpers/parse-to-number.helper';
import { UserAssignmentDto, AssignDto } from '../../shared/models/assignment.model';
import { AssignmentStatus, AssignmentStatusLabel } from '../../shared/constants/assignment-status.constant';
import { Router } from '@angular/router';
import { FunctionResultType, FunctionResultTypeLabel } from '../../shared/constants/function-result-type.constant';
import { AssignmentRestrictionType } from '../../shared/constants/assignment-restriction-type';
import { RoleName } from '../../shared/constants/roles.constant';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Subject } from 'rxjs';
import { RestrictionModalComponent } from '../../shared/modals/restriction/restriction-modal.component';
import { AssignmentRestrictionService } from '../../shared/services/assignment-restriction.service';
import { ModalRestrictionDto, RestrictionDto } from '../../shared/models/restriction.model';
import { restrictionModalHeaders } from '../../shared/translations/restriction.translation';
import { MarkDto, MarkModalDto } from '../../shared/models/mark.model';
import { MarkService } from '../../shared/services/mark.service';
import { MarkModalComponent } from '../mark-modal/mark-modal.component';

@Component({
  selector: 'app-assignment-list',
  templateUrl: './assignment-list.component.html',
  imports: [FormsModule, CommonModule]
})
export class AssignmentListComponent {
  constructor(public assignmentService: AssignmentService, public jwtService: JWTTokenService,
    public router: Router, private modalService: NgbModal, private restrictionService: AssignmentRestrictionService,
    private markService: MarkService
  ) {
  }

  ngOnInit(): void {
    this.getAssignmentsByUserId();

    let userRole: string | string[] = this.jwtService.getRoles();
    if (!Array.isArray(userRole)) {
      this.currentUserRole = userRole;
    } else if ((userRole as string[]).length === 1) {
      this.currentUserRole = userRole[0];
    }
  }

  currentUserRole!: string;

  assignments: UserAssignmentDto[] = [];

  activeAssignments: UserAssignmentDto[] = [];
  finishedAssignments: UserAssignmentDto[] = [];

  updateActiveFinishedAssignments() {
    this.updateActiveAssignments()
    this.updateFinishedAssignments();
  }

  getAssignmentsByUserId() {
    this.assignmentService.get().subscribe({
      next: (userAssignmentDtos: UserAssignmentDto[]) => {
        this.assignments = [];
        for (const userAssignmentDto of userAssignmentDtos) {
          this.assignments.push(userAssignmentDto)
        }

        this.updateActiveFinishedAssignments();
      }
    })
  }

  startContinueAssignment(assignment: UserAssignmentDto) {
    this.assignmentService.startContinue(assignment.id).subscribe({
      next: () => {
        this.router.navigate([`assignments/${assignment.id}`], { queryParams: { answerMode: 'false' } });
      }
    })
  }

  assign() {
    let userId = parseToNumber(this.jwtService.getId());
    this.assignmentService.assign(new AssignDto(userId)).subscribe({
      next: () => {
        this.getAssignmentsByUserId();
      }
    })
  }

  setRestriction(id: number) {
    const modalRef = this.modalService.open(RestrictionModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    this.restrictionService.getCurrent(id).subscribe({
      next: (dto: RestrictionDto) => {
        modalRef.componentInstance.inputValue = new ModalRestrictionDto(dto.restrictionType,
          dto.attemptsRemaining, dto.minutesForAttempt, dto.deadline);
      }
    })

    const errorSubject = new Subject<string>();

    modalRef.componentInstance.title = restrictionModalHeaders['non-default'];
    modalRef.componentInstance.errorSubject$ = errorSubject;

    modalRef.componentInstance.saveAttempt.subscribe(
      (value: ModalRestrictionDto) => {
        let dto = new RestrictionDto(value.restrictionType, null, null, null, id,
          value.attemptsRemaining, value.minutesForAttempt, value.deadline);
        this.restrictionService.setRestriction(dto).subscribe({
          complete: () => {
            modalRef.close();
            this.getAssignmentsByUserId();
          }
        })
      }
    );
  }

  markAssignment(id: number) {
    const modalRef = this.modalService.open(MarkModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    let markDto: MarkDto;
    this.markService.getByUserAssignmentId(id).subscribe({
      next: (dto: MarkDto[]) => {
        if (dto.length > 0) {
          modalRef.componentInstance.inputValue = new MarkModalDto(dto[0].mark, dto[0].comment);
          markDto = dto[0];
        }
      }
    })

    modalRef.componentInstance.saveAttempt.subscribe(
      (value: MarkModalDto) => {
        let newMarkDto;
        if (markDto)
          newMarkDto = new MarkDto(markDto.id, value.mark, value.comment);
        else
          newMarkDto = new MarkDto(null, value.mark, value.comment);    

        this.markService.mark(id, newMarkDto).subscribe({
          complete: () => {
            modalRef.close();
            this.getAssignmentsByUserId();
          }
        })
      }
    );
  }

  goToAnswersPage(assignment: UserAssignmentDto) {
    this.router.navigate([`assignments/${assignment.id}`], { queryParams: { answerMode: 'true' } });
  }

  isAssignedStatus(assignment: UserAssignmentDto) {
    return assignment.status === AssignmentStatus.ASSIGNED;
  }

  isInProgressStatus(assignment: UserAssignmentDto) {
    return assignment.status === AssignmentStatus.ACTIVE
  }

  getAssignmentStatusString(assignment: UserAssignmentDto) {
    let key = AssignmentStatus[assignment.status] as keyof typeof AssignmentStatusLabel;
    return AssignmentStatusLabel[key];
  }

  getFunctionResultTypeString(assignment: UserAssignmentDto) {
    let key = FunctionResultType[assignment.functionResultType] as keyof typeof FunctionResultTypeLabel;
    return FunctionResultTypeLabel[key];
  }

  updateActiveAssignments() {
    this.activeAssignments = this.assignments.filter(a => a.status !== AssignmentStatus.FINISHED);
  }

  updateFinishedAssignments() {
    this.finishedAssignments = this.assignments.filter(a => a.status === AssignmentStatus.FINISHED);
  }

  isRestrictionNAttempts(assignment: UserAssignmentDto) {
    return assignment.restrictionType === AssignmentRestrictionType.N_ATTEMPTS
  }

  isRestrictionDeadline(assignment: UserAssignmentDto) {
    return assignment.restrictionType === AssignmentRestrictionType.DEADLINE
  }

  isRestrictionAttemptInNMinutes(assignment: UserAssignmentDto) {
    return assignment.restrictionType === AssignmentRestrictionType.ATTEMPT_PER_N_MINUTES
  }

  isNextAttemptTimeAfterNow(assignment: UserAssignmentDto) {
    return new Date(assignment.nextAttemptTime).getTime() >= new Date().getTime()
  }

  isAdmin() {
    return this.currentUserRole === RoleName.ADMIN;
  }

  isTeacher() {
    return this.currentUserRole === RoleName.TEACHER;
  }

  isStudent() {
    return this.currentUserRole === RoleName.STUDENT;
  }
}
