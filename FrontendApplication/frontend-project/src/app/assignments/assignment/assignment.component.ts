import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AssignmentService } from '../../shared/services/assignment.service';
import { JWTTokenService } from '../../shared/services/jwt-token.service';
import { AssignmentAnswerDto, AssignmentDto, AssignmentResponseDto } from '../../shared/models/assignment.model';
import { assignmentLabels } from '../../shared/translations/assignment.translation';
import { joinVariables } from '../../shared/helpers/variables-join.helper';

@Component({
  selector: 'app-assignment',
  templateUrl: './assignment.component.html',
  styleUrl: './assignment.component.css',
  imports: [CommonModule, FormsModule]
})
export class AssignmentComponent {
  constructor(public assignmentService: AssignmentService, public jwtService: JWTTokenService,
    public router: Router, private route: ActivatedRoute) {
  }

  assignmentId!: number;
  assignmentDto!: AssignmentDto;
  answers: string[] = [];
  answersError = assignmentLabels['variable-empty'];

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.assignmentId = Number(params.get('id'));
      this.getById();
      for (let i = 0; i < this.assignmentDto.variablesCount; i++) {
        this.answers.push('');
      }
    });
  }

  getById() {
    this.assignmentService.getById(this.assignmentId).subscribe({
      next: (assignmentDto: AssignmentDto) => {
        this.assignmentDto = assignmentDto;
      },
      error: (error: any) => {
      }
    })
  }

  answer() {
    let variableNames = [], variableValues = [];
    for (let i = 0; i < this.answers.length; i++) {
      variableNames.push(`x${i + 1}`);
      variableValues.push(this.answers[i]);
    }
    let answer = joinVariables(variableNames, variableValues);

    this.assignmentService.answer(this.assignmentId, new AssignmentAnswerDto(answer))
      .subscribe({
        next: (assignmentResponseDto: AssignmentResponseDto) => {

        }
      })
  }

  stop() {
    this.assignmentService.stop(this.assignmentId).subscribe({
      next: () => {
        this.router.navigate([`/student/assignments`]);
      }
    })
  }

  finish() {
    this.assignmentService.finish(this.assignmentId).subscribe({
      next: () => {
        this.router.navigate([`/student/assignments`]);
      }
    })
  }

  validateAnswers() {

  }
}
