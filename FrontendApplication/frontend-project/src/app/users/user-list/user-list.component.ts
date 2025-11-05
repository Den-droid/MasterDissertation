import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { debounceTime, Subject } from 'rxjs';
import { ConfirmModalComponent } from '../../shared/modals/confirm/confirm-modal.component';
import { usersModal } from '../../shared/translations/user.translation';
import { UserDto } from '../../shared/models/user.model';
import { UserService } from '../../shared/services/user.service';
import { RoleName, RoleLabel } from '../../shared/constants/roles.constant';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  imports: [FormsModule]
})
export class UserListComponent implements OnInit {
  users: UserDto[] = [];
  filteredUsers: UserDto[] = [];

  searchQuery: string = '';
  searchSubject: Subject<string> = new Subject<string>();

  constructor(private modalService: NgbModal, private userService: UserService) { }

  ngOnInit(): void {
    this.getUsers();

    this.searchSubject.pipe(
      debounceTime(500)
    ).subscribe(value => {
      this.filteredUsers = this.users.filter(u => {
        let fullName = u.firstName + ' ' + u.lastName;
        return fullName.toLowerCase().includes(value.toLowerCase())
      }
      );
    });
  }

  getUsers() {
    this.userService.get(-1).subscribe({
      next: (usersDto: UserDto[]) => {
        this.users = usersDto;
        this.filteredUsers = usersDto;
      }
    })
  }

  approve(id: number) {
    const modalRef = this.modalService.open(ConfirmModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    modalRef.componentInstance.content = usersModal['approve-body'];
    modalRef.componentInstance.title = usersModal['approve-header'];

    modalRef.result.then(
      () => {
        this.userService.approve(id).subscribe({
          complete: () => {
            this.getUsers();
          }
        })
      }
    ).catch(
      () => { return; }
    )
  }

  reject(id: number) {
    const modalRef = this.modalService.open(ConfirmModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    modalRef.componentInstance.content = usersModal['reject-body'];
    modalRef.componentInstance.title = usersModal['reject-header'];

    modalRef.result.then(
      () => {
        this.userService.reject(id).subscribe({
          complete: () => {
            this.getUsers();
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

  getRoleLabel(roleName: string) {
    const key = Object.keys(RoleName).find(
      k => RoleName[k as keyof typeof RoleName] === roleName
    );
    return RoleLabel[key as keyof typeof RoleLabel];
  }
}
