import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

type ButtonVariant = 'primary' | 'secondary' | 'danger';
type ButtonSize = 'sm' | 'md' | 'lg';
type ButtonType = 'button' | 'submit' | 'reset';

@Component({
  selector: 'button-shared',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './button-shared.component.html',
  styleUrl: './button-shared.component.scss'
})
export class ButtonSharedComponent {
  @Input() label?: string;
  @Input() variant: ButtonVariant = 'primary';
  @Input() size: ButtonSize = 'md';
  @Input() type: ButtonType = 'button';
  @Input() disabled = false;
  @Input() href?: string;
  @Input() routerLink?: string | any[];
  @Input() target?: string;
  @Input() rel?: string;

  get isLink(): boolean {
    return !!this.href || !!this.routerLink;
  }

  get classes(): string {
    return [
      'btn-shared',
      `variant-${this.variant}`,
      `size-${this.size}`,
      this.disabled ? 'is-disabled' : ''
    ]
      .filter(Boolean)
      .join(' ');
  }
}
