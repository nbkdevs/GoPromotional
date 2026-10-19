"""gopromotional-siplog: daily water goal calculator and hydration log."""
from .core import ML_PER_US_FL_OZ, Entry, HydrationLog, daily_goal, parse_amount

__version__ = "1.0.0"
__all__ = ["ML_PER_US_FL_OZ", "Entry", "HydrationLog", "daily_goal", "parse_amount", "__version__"]
