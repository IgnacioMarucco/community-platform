import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

type AvatarSize = 'sm' | 'md' | 'lg';

@Component({
  selector: 'avatar-shared',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './avatar-shared.component.html',
  styleUrl: './avatar-shared.component.scss'
})
export class AvatarSharedComponent {
  @Input() src?: string;
  @Input() alt = 'avatar';
  @Input() size: AvatarSize = 'md';

  get sizeClass(): string {
    return `avatar-${this.size}`;
  }
}
