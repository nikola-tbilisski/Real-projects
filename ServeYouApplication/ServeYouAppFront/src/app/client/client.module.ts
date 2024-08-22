import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClientRoutingModule } from './client-routing.module';
import { ClientComponent } from './client.component';
import { ClientDashBoardComponent } from './pages/client-dash-board/client-dash-board.component';


@NgModule({
  declarations: [
    ClientComponent,
    ClientDashBoardComponent
  ],
  imports: [
    CommonModule,
    ClientRoutingModule
  ]
})
export class ClientModule { }
