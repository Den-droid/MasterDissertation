import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AssignmentService } from '../../shared/services/assignment.service';
import { JWTTokenService } from '../../shared/services/jwt-token.service';
import { parseToNumber } from '../../shared/helpers/parse-to-number.helper';
import { Assignment, parseUserAssignmentDtoToAssignment, UserAssignmentDto } from '../../shared/models/assignment.model';
import { AssignmentStatus, AssignmentStatusLabel } from '../../shared/constants/assignment-status.constant';
import { Router } from '@angular/router';
import { FunctionResultType, FunctionResultTypeLabel } from '../../shared/constants/function-result-type.constant';
import { AnswerDto } from '../../shared/models/answer.model';

@Component({
  selector: 'app-student-assignments',
  templateUrl: './student-assignments.component.html',
  imports: [FormsModule, CommonModule]
})
export class StudentAssignmentsComponent implements OnInit {
  constructor(public assignmentService: AssignmentService, public jwtService: JWTTokenService,
    public router: Router
  ) {
  }

  ngOnInit(): void {
    this.getAssignmentsByUserId();
  }

  @Input()
  assignments: Assignment[] = [];

  activeAssignments: Assignment[] = [];
  finishedAssignments: Assignment[] = [];

  answersToAssignments: Map<number, AnswerDto[]> = new Map<number, AnswerDto[]>();

  ngOnChanges() {
    this.updateActiveAssignments()
    this.updateFinishedAssignments();
  }

  getAssignmentsByUserId() {
    let userId = parseToNumber(this.jwtService.getId());
    this.assignmentService.getByUserId(userId).subscribe({
      next: (userAssignmentDtos: UserAssignmentDto[]) => {
        this.assignments = [];
        for (const userAssignmentDto of userAssignmentDtos) {
          this.assignments.push(parseUserAssignmentDtoToAssignment(userAssignmentDto))
        }

        this.getAnswersForAssignments();
      }
    })
  }

  getAnswersForAssignments() {
    let ids = this.assignments.map(a => a.id);

    this.assignmentService.getAnswersForAssignments(ids).subscribe({
      next: (answersDto: AnswerDto[][]) => {
        for (let i = 0; i < this.assignments.length; i++) {
          this.answersToAssignments.set(this.assignments[i].id, answersDto[i]);
        }
      }
    })
  }

  startContinueAssignment(assignment: Assignment) {
    this.assignmentService.startContinue(assignment.id).subscribe({
      next: () => {
        this.router.navigate([`assignments/${assignment.id}`]);
      }
    })
  }

  goToAnswersPage(assignment: Assignment) {
    this.router.navigate([`assignments/${assignment.id}/answers`]);
  }

  assign() {
    let userId = parseToNumber(this.jwtService.getId());
    this.assignmentService.assign(userId).subscribe({
      next: () => {
        this.getAssignmentsByUserId();
      }
    })
  }

  isAssignedStatus(assignment: Assignment) {
    return assignment.status === AssignmentStatus.ASSIGNED;
  }

  isInProgressStatus(assignment: Assignment) {
    return assignment.status === AssignmentStatus.ACTIVE || assignment.status === AssignmentStatus.STOPPED
      || assignment.status === AssignmentStatus.CORRECT_ANSWER_STOPPED;
  }

  getAssignmentStatusString(assignment: Assignment) {
    let key = AssignmentStatus[assignment.status] as keyof typeof AssignmentStatusLabel;
    return AssignmentStatusLabel[key];
  }

  getFunctionResultTypeString(assignment: Assignment) {
    let key = FunctionResultType[assignment.functionResultType] as keyof typeof FunctionResultTypeLabel;
    return FunctionResultTypeLabel[key];
  }

  updateActiveAssignments() {
    this.activeAssignments = this.assignments.filter(a => a.status != AssignmentStatus.FINISHED);
  }

  updateFinishedAssignments() {
    this.finishedAssignments = this.assignments.filter(a => a.status = AssignmentStatus.FINISHED);
  }
}
