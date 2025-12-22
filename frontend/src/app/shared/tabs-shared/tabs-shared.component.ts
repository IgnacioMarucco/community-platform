import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

export interface TabsSharedItem {
  label: string;
  link: string | any[];
}

type TabsSize = 'sm' | 'md' | 'lg';

@Component({
  selector: 'tabs-shared',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './tabs-shared.component.html',
  styleUrl: './tabs-shared.component.scss'
})
export class TabsSharedComponent {
  @Input() items: TabsSharedItem[] = [];
  @Input() size: TabsSize = 'md';

  get sizeClass(): string {
    return `tabs-${this.size}`;
  }
}
