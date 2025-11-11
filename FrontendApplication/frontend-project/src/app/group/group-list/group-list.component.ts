import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { debounceTime, Subject } from 'rxjs';
import { InfoModalComponent } from '../../shared/modals/info/info-modal.component';
import { ConfirmModalComponent } from '../../shared/modals/confirm/confirm-modal.component';
import { modalText } from '../../shared/translations/common.translation';

import { ActionType } from '../../shared/constants/action-type';
import { JoinPipe } from "../../shared/pipes/join.pipe";
import { GroupService } from '../../shared/services/group.service';
import { GroupDto } from '../../shared/models/group.model';
import { groupModals } from '../../shared/translations/group.translation';
import { AssignmentService } from '../../shared/services/assignment.service';
import { AssignToGroupDto } from '../../shared/models/assignment.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-group-list',
  templateUrl: './group-list.component.html',
  imports: [FormsModule, JoinPipe]
})
export class GroupListComponent implements OnInit {
  groups: GroupDto[] = [];
  filteredGroups: GroupDto[] = [];

  searchQuery: string = '';
  searchSubject: Subject<string> = new Subject<string>();

  constructor(private modalService: NgbModal, private groupService: GroupService, private assignmentService:
    AssignmentService, private readonly router: Router,
  ) { }

  ngOnInit(): void {
    this.getGroups();

    this.searchSubject.pipe(
      debounceTime(500)
    ).subscribe(value => {
      this.filteredGroups = this.groups.filter(g =>
        g.name.toLowerCase().includes(value.toLowerCase())
      );
    });
  }

  getGroups() {
    this.groupService.getAll().subscribe({
      next: (dto: GroupDto[]) => {
        this.groups = dto;
        this.filteredGroups = dto;
      }
    })
  }

  getStudentsPreview(id: number) {
    let group = this.filteredGroups.filter(fg => fg.id === id)[0];
    let students = [...group.students];
    let studentsCount = students.length;
    let studentsPreview;
    if (studentsCount > 3) {
      studentsPreview = students.slice(0, 3).map(s => s.firstName + " " + s.lastName);
    } else {
      studentsPreview = students.slice(0, studentsCount).map(s => s.firstName + " " + s.lastName);
    }
    return studentsPreview;
  }

  getAllStudents(id: number) {
    let group = this.filteredGroups.filter(fg => fg.id === id)[0];
    let students = [...group.students];
    let studentNames = students.map(s => s.firstName + " " + s.lastName);
    let studentsString = studentNames.join('\n');

    const modalRef = this.modalService.open(InfoModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    modalRef.componentInstance.content = studentsString;
    modalRef.componentInstance.title = groupModals['students-header'];
  }

  getSubjectsPreview(id: number) {
    let group = this.filteredGroups.filter(fg => fg.id === id)[0];
    let subjects = [...group.subjects];
    let subjectsCount = subjects.length;
    let subjectsPreview;
    if (subjectsCount > 3) {
      subjectsPreview = subjects.slice(0, 3).map(s => s.name);
    } else {
      subjectsPreview = subjects.slice(0, subjectsCount).map(s => s.name);
    }
    return subjectsPreview;
  }

  getAllSubjects(id: number) {
    let group = this.filteredGroups.filter(fg => fg.id === id)[0];
    let subjects = [...group.subjects];
    let subjectNames = subjects.map(s => s.name);
    let subjectsString = subjectNames.join('\n');

    const modalRef = this.modalService.open(InfoModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    modalRef.componentInstance.content = subjectsString;
    modalRef.componentInstance.title = groupModals['subjects-header'];
  }

  assignToGroup(id: number) {
    this.assignmentService.assignToGroup(new AssignToGroupDto(id)).subscribe({
      error: (error: any) => {
        const modalRef = this.modalService.open(InfoModalComponent, {
          centered: true,
          backdrop: 'static',
          keyboard: false
        });

        modalRef.componentInstance.content = error.error.message;
        modalRef.componentInstance.title = modalText['failure-header'];
      },
      complete: () => {
        const modalRef = this.modalService.open(InfoModalComponent, {
          centered: true,
          backdrop: 'static',
          keyboard: false
        });

        modalRef.componentInstance.content = groupModals['assign-to-group-text'];
        modalRef.componentInstance.title = modalText['success-header'];
      }
    });
  }

  addGroup() {
    this.router.navigate([`/add`]);
  }

  updateGroup(id: number) {
    this.router.navigate([`/${id}`]);
  }

  deleteGroup(id: number) {
    const modalRef = this.modalService.open(ConfirmModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    modalRef.componentInstance.content = modalText['confirm-content']
    modalRef.componentInstance.title = modalText['confirm-title'];

    modalRef.result.then(
      () => {
        this.groupService.delete(id).subscribe({
          complete: () => {
            this.getGroups();
          }
        })
      }
    ).catch(
      () => { return; }
    )
  }

  onSearchChange(value: string) {
    this.searchSubject.next(value);
  }
}
