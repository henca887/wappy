from django.db import models
from backend.shareables.models import ShareableItem


class Bookmark(ShareableItem):
    name = models.CharField(max_length=256)
    url = models.CharField(max_length=256)

    def as_tree_node_dict(self):
        return {'name': self.name,
                'leaf': 1,
                'url': self.url,
                'item_id': self.id}
