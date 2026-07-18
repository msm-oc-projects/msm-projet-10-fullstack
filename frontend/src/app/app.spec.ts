import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { App } from './app';

describe('App', () => {
  let fixture: ComponentFixture<App>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(App);
    fixture.detectChanges();
  });

  it('shows the chat proof of concept title', () => {
    expect(fixture.nativeElement.querySelector('h1').textContent).toContain(
      'Preuve de concept du tchat',
    );
  });

  it('starts with the accessible join form', () => {
    expect(fixture.nativeElement.querySelector('label[for="author"]')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('button[type="submit"]')).toBeTruthy();
  });
});
