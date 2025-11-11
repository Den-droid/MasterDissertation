import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GroupListComponent } from './group-list/group-list.component';
import { groupTitles } from '../shared/translations/group.translation';
import { GroupUpsertComponent } from './group-upsert/group-upsert.component';

const groupRoutes: Routes = [
  { path: "", component: GroupListComponent, title: groupTitles.groups },
  { path: "add", component: GroupUpsertComponent, title: groupTitles['add-group'] },
  { path: ":id", component: GroupUpsertComponent, title: groupTitles['update-group'] }
]

@NgModule({
  imports: [
    RouterModule.forChild(groupRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class GroupModule { }
