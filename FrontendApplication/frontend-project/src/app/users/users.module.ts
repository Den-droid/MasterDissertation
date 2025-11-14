import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserListComponent } from './user-list/user-list.component';
import { usersPageTitles } from '../shared/translations/user.translation';
import { UserPermissionComponent } from './user-permission/user-permission.component';

const usersRoutes: Routes = [
  { path: "", component: UserListComponent, title: usersPageTitles.users },
  { path: ":id/permissions", component: UserPermissionComponent, title: usersPageTitles['user-permissions'] }
]

@NgModule({
  imports: [
    RouterModule.forChild(usersRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class UsersModule { }
