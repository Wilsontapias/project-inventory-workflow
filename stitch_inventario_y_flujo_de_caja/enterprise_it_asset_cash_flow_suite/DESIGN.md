---
name: Enterprise IT Asset & Cash Flow Suite
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#45464d'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#76777d'
  outline-variant: '#c6c6cd'
  surface-tint: '#565e74'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#131b2e'
  on-primary-container: '#7c839b'
  inverse-primary: '#bec6e0'
  secondary: '#545f73'
  on-secondary: '#ffffff'
  secondary-container: '#d5e0f8'
  on-secondary-container: '#586377'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#001a42'
  on-tertiary-container: '#3980f4'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dae2fd'
  primary-fixed-dim: '#bec6e0'
  on-primary-fixed: '#131b2e'
  on-primary-fixed-variant: '#3f465c'
  secondary-fixed: '#d8e3fb'
  secondary-fixed-dim: '#bcc7de'
  on-secondary-fixed: '#111c2d'
  on-secondary-fixed-variant: '#3c475a'
  tertiary-fixed: '#d8e2ff'
  tertiary-fixed-dim: '#adc6ff'
  on-tertiary-fixed: '#001a42'
  on-tertiary-fixed-variant: '#004395'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 38px
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  title-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
  title-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
  body-lg:
    fontFamily: Inter
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 22px
  body-md:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 18px
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 14px
  numeric-metric:
    fontFamily: Roboto Mono
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 34px
  numeric-table:
    fontFamily: Roboto Mono
    fontSize: 13px
    fontWeight: '500'
    lineHeight: 18px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1.5rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 0.75rem
  space-lg: 1.25rem
  space-xl: 2rem
---

## Brand & Style
This design system is engineered for enterprise IT hardware asset management, capital expenditure tracking, and operational cash flow monitoring. It directly addresses the daily workflows of procurement directors, IT infrastructure managers, and financial controllers overseeing high-value computing assets—such as rack servers, enterprise fleet laptops, discrete components, and modular UPS systems.

The emotional baseline of the interface is unyielding precision, institutional authority, and rigorous clarity. In environments dealing with critical infrastructure stockouts and large-scale currency allocations (Colombian Pesos, COP), ambiguity is an operational risk. The aesthetic aligns with **Corporate Analytical Modernism**: dense, high-utility information display balanced by generous structural rhythm, ultra-clean geometry, disciplined whitespace, and sharp contrast boundaries.

Visual clutter is systematically eliminated. Instead of ornamental embellishments, visual hierarchy is driven by crisp borders, tonal surface compartmentalization, and unambiguous semantic color signaling.

## Colors
The color architecture relies on a multi-tiered corporate navy and slate foundation, layered over an engineered cool-neutral base (`#F8FAFC`). Primary actions and brand foundations anchor to Deep Slate Navy (`#0F172A`), supported by Slate Blue (`#1E293B`) for secondary navigational elements, toolbars, and table headers. Interactive digital focal points use Electric Blue (`#3B82F6`).

Semantic color assignments carry explicit analytical definitions to ensure instant cognitive recognition without relying solely on color (paired with text labels and status icons):
- **Stock Depletion & Critical Alerts (Vermilion Red):** `#EF4444`. Applied to stockouts, lead-time violations, negative operational cash flows, and destructive actions. Background tint: `#FEF2F2`, border tint: `#FECACA`.
- **Low Stock & Stagnant Inventory (Amber Warning):** `#F59E0B`. Applied to items approaching minimum safety thresholds, pending reconciliations, or delayed vendor invoices. Background tint: `#FFFBEB`, border tint: `#FDE68A`.
- **Optimal & Healthy Flow (Emerald Green):** `#10B981`. Denotes optimal stock levels, verified equipment handovers, and positive COP margin movements. Background tint: `#ECFDF5`, border tint: `#A7F3D0`.

Surfaces:
- **Canvas Base:** `#F8FAFC`
- **Surface Elevation 1 (Cards, Modals, Panels):** `#FFFFFF`
- **Surface Elevation 2 (Table Headers, Inset Well, Filter bars):** `#F1F5F9`
- **Borders & Dividers:** `#E2E8F0` (Default hairline) and `#CBD5E1` (Input/Active state)
- **Primary Text:** `#0F172A`
- **Muted Text / Column Subheads:** `#64748B`

## Typography
Typography is split into two specialized engines:
1. **Interface Engine (Inter):** Leveraged for all interface copy, metadata, structural headers, and navigational states. Tight optical kerning and tall x-height guarantee dense, fatigue-free reading across sprawling enterprise inventory grids.
2. **Tabular & Financial Engine (Roboto Mono / Tabular Numbers):** Mandatory for all numeric metrics, serial numbers, SKU designations, unit counts, and Colombian Peso (COP) currency statements (e.g., `$ 452.890.000 COP`). Tabular glyph widths preserve column alignment during continuous vertical scanning.

Guidelines for COP display:
- Right-align all monetary values in data tables and ledger summaries.
- Precede monetary figures with the currency indicator `$` and follow with `COP` in table summaries or KPI cards to prevent cross-border procurement ambiguity.
- Always render trailing decimals or localized thousand-point separators consistently (`.` for thousands, `,` for decimals).

## Layout & Spacing
The layout follows a fluid-grid structure designed specifically for desktop displays (1440px+ native targets, scaling down to 1024px minimum desktop viewports). It employs a 12-column architecture optimized for split viewports: an explicit 260px fixed primary navigation sidebar, accompanied by a dynamic workspace grid utilizing 1rem gutters and 1.5rem canvas margins.

Compact data density dictates component spacing:
- **Vertical Rhythm in Tables:** Compact row heights are locked to 40px (default) or 48px (spacious view mode), using `space-xs` (4px) and `space-sm` (8px) internal cell paddings.
- **Form Groups & Filters:** Faceted filter bars apply `space-sm` (8px) between chips and controls, ensuring maximum tools-per-pixel density without sacrificing click target viability.
- **Dashboard Modular Cards:** Structured on `space-lg` (20px) outer padding, with `space-md` (12px) separating metric aggregates from contextual trends and delta badges.

## Elevation & Depth
Elevation in this system eschews heavy drop shadows in favor of a crisp, architectural layer model characterized by **low-contrast outlines, tonal surface stacking, and ambient micro-shadows**.

1. **Surface Layering:**
   - **Level 0 (Canvas):** Tone `#F8FAFC`. Provides clean contrast for cards and sheets.
   - **Level 1 (Cards, Data Grids, Sidebars):** Solid `#FFFFFF` enclosed by a 1px border of `#E2E8F0`. Shadow is subtle and diffused: `0 1px 2px 0 rgba(15, 23, 42, 0.04)`.
   - **Level 2 (Popovers, Facet Dropdowns, Sticky Header Bars):** Solid `#FFFFFF`, border 1px `#CBD5E1`, shadow `0 4px 6px -1px rgba(15, 23, 42, 0.07), 0 2px 4px -2px rgba(15, 23, 42, 0.05)`.
   - **Level 3 (Modals, Slide-over Drawers):** Solid `#FFFFFF`, 1px border of `#CBD5E1`, shadow `0 20px 25px -5px rgba(15, 23, 42, 0.12), 0 8px 10px -6px rgba(15, 23, 42, 0.08)`. Backdrops feature a 40% opacity navy veil (`#0F172A` with 40% alpha) and `backdrop-filter: blur(2px)`.

2. **Table Outlines:**
   - Row borders use `#F1F5F9`. Selected rows use a `#EFF6FF` background tint with a 2px left border accent in `#3B82F6`.

## Shapes
A constrained "Soft" geometry (level 1) governs the design language, delivering the structural rigidity expected of professional accounting and inventory tooling:
- **Base Components (Inputs, Buttons, Dropdowns):** `0.25rem` (4px). Instills a disciplined, engineered feel.
- **Cards, Filter Wells, Tables, Modal Containers:** `rounded-lg` at `0.5rem` (8px). Softens boundaries without wasting peripheral real estate.
- **Semantic Badges & Status Chips:** `rounded-full` or capped at `0.25rem` (4px) to retain technical uniformity.

## Components

### 1. Data Tables & Pagination
- **Header:** `#F8FAFC` background, text in `label-sm` uppercase (`#64748B`), 1px bottom border in `#E2E8F0`. Includes column sorting indicators and multi-select master checkboxes.
- **Rows:** Alternating subtle hover state (`#F8FAFC`), 40px cell height. Monospace values (`numeric-table`) for asset tags, serials, and COP amounts.
- **Pagination Footer:** Attached to the bottom table frame. Displays active entry count range (e.g., "1–50 de 1.420 servidores"), per-page dropdown selector, and compact previous/next directional buttons with direct page jumps.

### 2. Faceted Filter Toolbar
- Composed of grouped inputs: text search (SKU/Model/Serial) with left search glyph, multi-select dropdown pills (Category: Servidores, Laptops, UPS; State: Óptimo, Crítico, Bajo), and date-range pickers for cash flow tranches.
- Active filters display as compact chips with dismissible `×` icons and background `#F1F5F9`. Includes an explicit "Limpiar Filtros" text button.

### 3. Semantic Badges
- Strict composition: Container (`rounded`, 20px height) + 12px SVG icon + descriptive text label.
- **Crítico / Quiebre:** Background `#FEF2F2`, border `#FECACA`, text `#B91C1C`, icon `AlertTriangle`.
- **Stock Bajo / Estancado:** Background `#FFFBEB`, border `#FDE68A`, text `#B45309`, icon `AlertCircle`.
- **Óptimo / Saludable:** Background `#ECFDF5`, border `#A7F3D0`, text `#047857`, icon `CheckCircle2`.

### 4. KPI Financial & Stock Metric Cards
- Container: Surface Level 1 card with `space-lg` padding.
- Structure:
  - Top: Label (`title-md` `#64748B`) paired with an enterprise domain icon (e.g., Server Rack, Wallet).
  - Center: Tabular numeric value (`numeric-metric` `#0F172A`), formatted with standard COP prefixes.
  - Bottom: Delta indicator containing an arrow icon, percentage variation (e.g., `+12.4% vs mes anterior` using `#10B981`, or `-4.2% flujo neto` using `#EF4444`), followed by an explanatory caption in `body-sm` (`#64748B`).

### 5. Input Fields & Selects
- Height: 36px (compact desktop standard).
- Base: White background, 1px border `#CBD5E1`, text `body-md` `#0F172A`, placeholder `#94A3B8`.
- Focus state: Border `#3B82F6` accompanied by a 2px outer glow (`rgba(59, 130, 246, 0.15)`).

### 6. Buttons
- **Primary:** Background `#0F172A`, hover `#1E293B`, text `#FFFFFF`, border none, 36px height, font `title-md`.
- **Secondary:** Background `#FFFFFF`, border 1px `#CBD5E1`, text `#1E293B`, hover `#F8FAFC`.
- **Destructive:** Background `#EF4444`, hover `#DC2626`, text `#FFFFFF`.
- **Tertiary/Ghost:** Transparent background, text `#3B82F6`, hover `#EFF6FF`.

### 7. Confirmation Modals & Notifications
- **Modals:** Fixed width 480px–640px. Header carries severe semantic title for asset de-registrations or write-offs. Secondary confirmation requires typing the SKU or exact confirmation string before unlocking the destructive submit button.
- **Notification Banners / Toasts:** Floating 360px cards, top-right dock. Semantic border accent on the left margin (3px solid), featuring a bold status title, actionable message, and dismiss button.