"""gopromotional-mugday: caffeine and coffee intake tracker."""
from .core import (DEFAULT_HALF_LIFE_HOURS, DRINKS, LIMITS, CaffeineLog, Drink, from_minutes, lookup_drink,
                   now_local, to_minutes)

__version__ = "1.0.0"
__all__ = ["DEFAULT_HALF_LIFE_HOURS", "DRINKS", "LIMITS", "CaffeineLog", "Drink", "from_minutes", "lookup_drink",
           "now_local", "to_minutes", "__version__"]
