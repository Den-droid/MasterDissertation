import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AssignmentService } from '../../shared/services/assignment.service';
import { JWTTokenService } from '../../shared/services/jwt-token.service';
import { parseToNumber } from '../../shared/helpers/parse-to-number.helper';
import { Assignment, parseUserAssignmentDtoToAssignment, UserAssignmentDto } from '../../shared/models/assignment.model';
import { AssignmentStatus } from '../../shared/constants/assignment-status.constant';

@Component({
  selector: 'app-student-assignments',
  templateUrl: './assignment-list.component.html',
  styleUrls: ['./assignment-list.component.css'],
  imports: [FormsModule, CommonModule]
})
export class AssignmentsComponent {
  constructor(public assignmentService: AssignmentService, public jwtService: JWTTokenService) {
  }

  @Input()
  assignments: Assignment[] = [];

  activeAssignments: Assignment[] = [];
  finishedAssignments: Assignment[] = [];

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
      }
    })
  }

  assign() {
    let userId = parseToNumber(this.jwtService.getId());
    this.assignmentService.assign(userId).subscribe({
      next: () => {
        this.getAssignmentsByUserId();
      }
    })
  }

  updateActiveAssignments() {
    this.activeAssignments = this.assignments.filter(a => a.status != AssignmentStatus.FINISHED);
  }

  updateFinishedAssignments() {
    this.finishedAssignments = this.assignments.filter(a => a.status = AssignmentStatus.FINISHED);
  }
}
